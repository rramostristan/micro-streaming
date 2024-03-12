# Prueba Técnica de Micro Streaming Analytics

A continuación, explicaré las instrucciones necesarias para desplegar la solución propuesta en su máquina local de desarrollo, además de explicar las decisiones tomadas durante el desarrollo.


## Instrucciones para desplegar la solución

* En primer lugar, dado que se ha desarrollado una solución basada en contenedores, es necesario tener instalado Docker en su máquina.

* En este directorio se encuentran los archivos docker-compose.yml, que permiten levantar la infraestructura necesaria para que la aplicación funcione, además de un Dockerfile que permite generar una imagen con la aplicación de Java Spring.

* Dado que el docker-compose asume que ya existe una imagen con la aplicación de Java, es necesario, en primer lugar, crear la imagen Docker. Para ello, deberá ejecutar en el terminal el siguiente comando: `docker build -t micro-streaming .`

* Este comando se encargará de generar una imagen denominada micro-streaming. En caso de querer usar otro nombre, deberá modificar en el docker-compose el nombre de la imagen del servicio micro-streaming con el nombre elegido.

* Con la imagen generada, solo será necesario ejecutar el archivo docker-compose. Para ello, se usará el comando `docker-compose up -d` para que se ejecute en segundo plano. Una vez que se quiera finalizar la ejecución de la aplicación, se usa el comando `docker-compose down`.

## Documentación del desarrollo

### Estructura JSON de los mensajes

Me he basado en la documentación de la API de integración de dispositivos de OpenGate proporcionada a la hora de crear la estructura de los mensajes que se enviarán a la cola en formato JSON.

Para ello, he creado la clase OpenGateMessage, la cual tendrá los datos de la versión de la estructura, el ID del dispositivo que ha recolectado la información, el path y el trustedBoot, además de una colección de objetos OpenGateDataStreamMessage, que representan los stream de datos que se han de recolectar.

La clase OpenGateDataStreamMessage contará con los campos ID del dataStream, el identificador del feed y una colección de objetos del tipo OpenGateDatapoint, que a su vez contiene los campos from, at y value. Este último será del tipo Object, aunque en este caso, a la hora de interpretarlo, dado que estamos obteniendo cálculos estadísticos, se tratará como un valor numérico del tipo int.

A continuación, se puede observar la estructura del mensaje en formato JSON:

```
{
  "version": "0.1",
  "device": "Device 1",
  "path": "192.168.0.1",
  "trustedBoot": "REST",
  "datastreams": [
    {
      "id": "temperature",
      "feed": "temperature",
      "datapoints": [{ "from": 0, "at": 1710193908758, "value": 11 }]
    }
  ]
}
```


### Estructura JSON para almacenar los resultados

Para la implementación de la funcionalidad de almacenar los resultados en una base de datos MongoDB, he seguido una estructura similar. He creado una clase denominada Statistics, que tendrá el ID del dispositivo como identificador del documento, el path, trustedBoot, version y DataStreams.

De esta manera, cada vez que se reciba un nuevo mensaje, se comprobará si existe un documento en base de datos con el mismo identificador del dispositivo. En caso afirmativo, recuperaremos dicho registro y guardaremos los nuevos análisis estadísticos. En caso negativo, generaremos un nuevo documento cuya clave será el identificador del dispositivo, apriori único.

Los dataStream estarán representados por la clase DataStream, que contará con el identificador del propio datastream, el feed y los resultados del análisis estadístico almacenados en formato JSON como se pide en el documento, además de la marca de tiempo en la que se ha producido dicho análisis.

De esta manera, un documento tendrá el siguiente aspecto representado en formato JSON.

Los resultados estadísticos se almacenarán en string en formato JSON generado a partir de la clase StatisticsResults, que almacenará las medidas de media, mediana, moda, desviación estándar, los cuartiles, máximo, mínimo y la fecha en la que se ha procesado. De esta forma, tendremos una estructura JSON de los mensajes de este tipo:

```
{
  "deviceId": "Device 1",
  "path": "192.168.0.1",
  "trustedBoot": "REST",
  "version": "0.1",
  "dataStreams": [
    {
      "id": "temperature",
      "feed": "temperature",
      "results": {
        "mean": 21.8,
        "median": 23,
        "mode": 0,
        "standardDeviation": 9.271461589199408,
        "quartiles": [19, 23, 28],
        "maximum": 33,
        "minimum": 0,
        "processedAt": "2024-03-10T21:51:42.492+00:00"
      }
    }
  ]
}
```

### Decisiones de Diseño

n este apartado, explicaré las decisiones que he tomado a la hora de desarrollar la aplicación para cumplir con los requisitos especificados.

En primer lugar, con el objetivo de seguir el patrón MVC, he dividido la lógica de la aplicación en diferentes carpetas que almacenan clases con un objetivo específico. Los controladores establecen los diferentes endpoints disponibles en nuestra API, los parámetros que aceptan y las respuestas que devuelven.

Estos controladores llamarán a los managers, que serán los encargados de manejar toda la lógica relacionada con la gestión de los datos y cualquier tipo de proceso. Los modelos establecerán las estructuras de los objetos con los que vamos a trabajar. En este sentido, cabe mencionar el uso de interfaces en los managers que declaran los métodos accesibles desde las mismas, ocultando métodos que no deben ser accesibles desde fuera de la implementación de la interfaz.

Todas las clases relacionadas con la gestión de RabbitMQ estarán en la misma carpeta, con un archivo de configuración que se encargará de crear el exchange, queue y binding necesarios para el correcto funcionamiento de la aplicación y que trabaje con variables configurables a partir del archivo de propiedades.

En esta carpeta se encuentra el consumidor, que se encarga de ejecutar de manera periódica el método encargado de recibir en batch los mensajes y procesarlos. Para que se ejecute de manera automática, se ha utilizado la anotación @Scheduled, que nos permite anotar métodos que queremos que se ejecuten automáticamente según las condiciones establecidas. También se ha creado un productor cuyo funcionamiento se puede deshabilitar a partir de una propiedad, que se encargará de crear unos mensajes periódicamente para que se pueda comprobar el funcionamiento de la aplicación sin la necesidad de enviar mensajes a la cola de manera manual. La frecuencia con la que se ejecutan ambos métodos también es configurable a través de las propiedades, ya sea con el archivo application.properties o a través de las variables de entorno especificadas en el docker-compose.yml.

También se ha creado una clase de utilería con el objetivo de transformar los mensajes recibidos por Rabbit en objetos con los cuales trabajar, además de ser utilizada para almacenar los análisis estadísticos de un datastream en formato JSON en la base de datos, como se ha explicado anteriormente.

En cuanto a consideraciones de seguridad y monitoreo, se ha hecho uso de loggers que permiten a los usuarios tener una idea de las acciones que lleva a cabo el servicio, además de registrar las posibles excepciones que puedan tener lugar y que son tratadas a través de estructuras try-catch.


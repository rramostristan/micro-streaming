# Prueba Técnica de Micro Streaming Analytics

A continuación, explicaré las instrucciones necesarias para desplegar la solución propuesta en su máquina local de desarrollo, además de explicar las decisiones tomadas durante el desarrollo.


## Instrucciones para desplegar la solución

* En primer lugar, dado que se ha desarrollado una solución basada en contenedores, es necesario tener instalado Docker en su máquina.

* En este directorio se encuentran los archivos docker-compose.yml, que permiten levantar la infraestructura necesaria para que la aplicación funcione, además de un Dockerfile que permite generar una imagen con la aplicación de Java Spring.

* Dado que el docker-compose asume que ya existe una imagen con la aplicación de Java, es necesario, en primer lugar, crear la imagen Docker. Para ello, deberá ejecutar el comando `mvn clean install` que se encargara de generar el jar usado en el Dockerfile, una vez ejecutado dicho comando se puede pasar a la creación de la imagen mediante el comando: `docker build -t micro-streaming .`

* Este comando se encargará de generar una imagen denominada micro-streaming. En caso de querer usar otro nombre, deberá modificar en el docker-compose el nombre de la imagen del servicio micro-streaming con el nombre elegido.

* Con la imagen generada, solo será necesario ejecutar el archivo docker-compose. Para ello, se usará el comando `docker-compose up -d` para que se ejecute en segundo plano. Una vez que se quiera finalizar la ejecución de la aplicación, se usa el comando `docker-compose down`.

* Cabe mencionar que se han expuesto los puertos para acceder tanta a Rabbit como a Mongo, por lo que en caso de que en su máquina ya se ejecuten estos servicios y ocupen los puertos por defecto, debera de cambiar los puertos mapeados del servicio en el docker-compose o bien comentarlos para que no se expongan.

* El swagger a partir del cual poder interactuar con la aplicación estar disponible por defecto en : http://localhost:8080/swagger-ui/index.html

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

Para la implementación de la funcionalidad de almacenar los resultados en una base de datos MongoDB, he seguido una estructura similar. He creado una clase denominada Statistics que se encargará de almacenar los resultados estadísticos y tendrá como campos el identificador del documento, la fecha en la cual se ha realizado el proceso, el número de mensajes procesados y los resultados obtenidos del análisis que se guardaran como se ha pedido en formato JSON.

Las propiedades del documento se deben a que dado que no se conocen con exactitud el número de mensajes que se van a procesar en un momento dado o si pueden pertenecer a distintos dispositivos no hay características en común que podamos extraer de los mensajes más alla del propio análisis estadístico, por lo cual serán estos los campos a partir de los cuales se generarán endpoints en la API con los cuales el usuario podrá filtrar los resultados.

En este sentido, existen métodos para obtener todos los documentos, filtrar por número de mensajes procesados, por el identificador y un endpoint que permite obtener los documentos procesados entre dos fechas, posteriores o anteriores a una.

Aquí cabe explicar que según mi entendimiento del problema, la idea es recorrer los datastreams de todos los mensajes que procesen en ese batch e iterar en cada dataStream a partir de los datapoints recopilando los values de cada uno que deberían ser de tipo int y una vez se hayan recolectado todos los valores de todos los mensajes, realizar sobre ellos el análisis estadístico. También se ha proporcionado una propiedad denominada `rabbitmq.batch.size` que permite limitar el número de mensajes que se procesan en cada batch.

Los resultados estadísticos se almacenarán en string en formato JSON generado a partir de la clase StatisticsResults, que almacenará las medidas de media, mediana, moda, desviación estándar, los cuartiles, máximo y mínimo. De esta forma, tendremos una estructura JSON de este tipo:

```
{
   "mean":135.6,
   "median":87.5,
   "mode":113,
   "standardDeviation":75.28,
   "quartiles":[
      135.5,
      87.5,
      93.5
   ],
   "maximum":283,
   "minimum":2
}
```

### Decisiones de Diseño

En este apartado, explicaré las decisiones que he tomado a la hora de desarrollar la aplicación para cumplir con los requisitos especificados.

En primer lugar, con el objetivo de seguir el patrón MVC, he dividido la lógica de la aplicación en diferentes carpetas que almacenan clases con un objetivo específico. Los controladores establecen los diferentes endpoints disponibles en nuestra API, los parámetros que aceptan y las respuestas que devuelven.

Estos controladores llamarán a los managers, que serán los encargados de manejar toda la lógica relacionada con la gestión de los datos y cualquier tipo de proceso como, por ejemplo, la validación de los campos que se han pasado como parámetros. Los modelos establecerán las estructuras de los objetos con los que vamos a trabajar. En este sentido, cabe mencionar el uso de interfaces en los managers que declaran los métodos accesibles desde las mismas, ocultando métodos que no deben ser accesibles desde fuera de la implementación de la interfaz.

Todas las clases relacionadas con la gestión de RabbitMQ estarán en la misma carpeta, con un archivo de configuración que se encargará de crear el exchange, queue y binding necesarios para el correcto funcionamiento de la aplicación y que hace uso de variables configurables a partir del archivo de propiedades. Por ejemplo, la propiedad `rabbitmq.consume.rate=40000` se encargará de que se ejecute el método de procesar en batch los mensajes de la cola cada 40 segundos dado que la propiedad está expresada en milisegundos.

Para que el consumidor se ejecute de manera automática, se ha utilizado la anotación @Scheduled, que nos permite anotar métodos que queremos que se ejecuten automáticamente según las condiciones establecidas. También se ha creado un productor cuyo funcionamiento se puede deshabilitar a partir de la propiedad `rabbitmq.producer.enabled` que se encargará de crear unos mensajes periódicamente para que se pueda comprobar el funcionamiento de la aplicación sin la necesidad de enviar mensajes a la cola de manera manual, en este caso habrá que esperar hasta que se haya ejecutado una vez el consumer para que aparezcan resultados en las llamadas API. La frecuencia con la que se ejecutan ambos métodos también es configurable a través de las propiedades como se ha mencionado antes, ya sea con el archivo application.properties o a través de las variables de entorno especificadas en el docker-compose.yml.

También se ha creado una clase utils con el objetivo de transformar los mensajes recibidos por Rabbit en formato JSON en objetos con los cuales trabajar, además de ser utilizada para almacenar los análisis estadísticos de un datastream en formato JSON en la base de datos, como se ha explicado anteriormente.

En cuanto a consideraciones de seguridad y monitoreo, se ha hecho uso de loggers que permiten a los usuarios tener una idea de las acciones que lleva a cabo el servicio, además de registrar las posibles excepciones que puedan tener lugar y que son tratadas a través de estructuras try-catch. Además, para aumentar la seguridad se podría hacer uso de JWTs para que el uso de la APi este restringido a usuarios autorizados.


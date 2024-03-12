# Prueba Tecnica de Micro Streaming Analytics

A continuación, voy a explicar las instrucciones necesarias para poder desplegar la solución propuesta
en su máquina local de desarrollo además de explicar las decisiones tomadas en el desarrollo.


## Instrucciones para desplegar la solución

* En primer lugar dado que se ha desarrollado una solución basada en contenedores es necesario que tenga instalado Docker en su maquina.

* En este directorio estan presentes los archivo docker-compose.yml que permite levantar la infraestructura necesaria para que la aplicación funcione ademas de un Dookerfile que permite generar una imagen con la aplicacion de Java Spring.

* Dado que el docker-compose asume que ya existe una imagen con la aplicacion de Java, es necesario es primer lugar crear la imagen Docker, para ello se debera ejecutar en el terminal el siguiente comando:         ` docker build -t micro-streaming .`

* Este comando se encargara de generar una imagen denominada micro-streaming, en caso de querer usar otro nombre, se debera de modificar en el docker-conmpose el nombre de la imagen del servicio micro-streaming con el nombre elegido

* Con la imagen generada, solo sera necesario ejecutar el archivo docker-compose, para ello se usara el comando `docker-compose up -d` para que se ejecute en segundo plano, una vez se quiera finalizar la ejecución de la aplicación se usa el comando `docker-compose down
  ` para detener los contenedores.

## Documentación del desarrollo

### Estructura JSON de los mensajes

Me he basado en la documentación de la a API de integración
de dispositivos de OpenGate poroporcionada a la hora de crear la estructura de los mensajes que se enviaran a la cola en formato JSON.

Para ello he creado la clase OpenGateMessage la cual tendra los datos de la versión de la estructura, el id del dispositvio que ha recolectado la información, el path y el trustedBoot, además de una colección de objetos OpenGateDataStreamMessage que representan los stream de datos que se han de recolectar.

La clase OpenGateDataStreamMessage contará con los campos id del dataStream, el identificador del feed y una coleccion de objetos del tipo OpenGateDatapoint que a su vez contiene los campos from, at y value, este ultimo sera del tipo Object, aunque en este caso a la hora de interpretarlos, dado que estamos obteniendo calculos estadisticos, a la hora de tratarlos se hara con la idea que contiene un valor númerico del tipo int.


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

Para la implementación de la funcionalidad de almacenar los resultados en una base de datos MongoDB, he seguido una estrucutra similar, he creado una clase denominada Statistics que tendra el id del dispositivo como identificador del document, el path, trustedBoot, version y DataStreams.

De esta manera cada vez que se reciba un nuevo mensaje, se compprobara si existe un documento en base de datos con el mismo identificador del dispositivo, en caso afirmativo recuperarmos dicho registro y guardaremos los nuevos analisis estadisticos, en caso negativo generaremos un nuevo documento cuya clave sera el identificador del dispositovo, aprioro unico.

Los dataStream estraran represnetados por la clase DataStream que contara con le idnetificador del propio datasream, el feed y los resutlados del anailisis estadistico almacenados en formato JSON como se pide en el documento ademas de la marca de tiempo en la que se ha prooduciod dicho analisis.

De esta manera un documento tendra el siguiente aspecto representado en formato JSON.

Los resultados estadistcios se almacenaran en string en fomrato JSON generado a partir de la clase StatisticsResults que almacenara las medidas de media, mediana, moda, desviacionEstandar, los cuartiles, maximo, miniumo y la fecha en la que se ha procesado, de esta forma tendremos un estructura JSON de los mensajes de este tipo:

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
      "resultsObject": {
        "media": 21.8,
        "mediana": 23,
        "moda": 0,
        "desviacionEstandar": 9.271461589199408,
        "cuartiles": [19, 23, 28],
        "maximo": 33,
        "minimo": 0,
        "processedAt": "2024-03-10T21:51:42.492+00:00"
      }
    }
  ]
}
```

### Decisiones de Diseño

En este apartado explicare las decisiones que he tomado a la hora de desarrollar la aplicacion para cumplir con los requisitos epecirficados.

En primer lugar, con el objetivo de seguir el patrón MVC he divido la lógica de la aplicación en diferentes carpetas que almacenen clases con un objetivo especifico, los controlodores donde se estableceran cuales son los diferentes endpoints disponibles en nuestra API, los parametros que aceptan, las respuestas que devuelven ...

Estos controladores llamaran a los managers que seran los encargados de manejar toda la lóögica relacionada con la gestión de los datos y cualquier tipo de proceso, los modelos donde se estableceran las estructuras de los objetos con los que vamos a treabajar. En este sentido, cabe mencionar el uso de interfaces en los managers que seran las encargadas de declarar cuales son los metodos accesibles desde las mismas, ocultando metodos que no devben ser accesibles desde fuera de la implementación de la interfaz.

Todas las clases relacionadas con la gestion de RabbitMQ estaran en la misma carpeta, con un archivo de configuracion que se encargara de crear el exchange, queue y bindning necesarios para el corercto funcionamiento de la apliacion y que trabajas con variables configurables a partir del archivo de propiedades.

cEn esta carpeta se encuntera el consumer que se encarga de ejecutar de manera periodica el metodo encargado de recibir en batch los mensajes y procesarlos, para que se ejeucte de manera periodica se ha hecho uso de la anotacion @Scheduled que nos permite anotar metodos que quweremos que se ejucten automticamente segun las condidicones estableciedas. Tambien se ha creado un producer cuyo funcionamiento se puede deshabilitar a partir de una propiedad que se encargara de crear unos mensajes de manera periodica para que se pueda comprobar el funcionamiento de la aplicacion sin la necesidad de enviar mensajes a la cola de manera manual. La frecuencia con la que se ejecutan ambos metodos tambien es configurable a traves de las propiedades, ya sea conb el archivo appliaction.porperties o tarves de las variables de entrono especificadas en el docker-compose.yml.

Tasmbien se ha creado una clase de utileria con el pobjetivo de transformar los mensajes recibidos por Rabbit a objetosd con los cuales trabajar ademas de tambien ser usado para almacenar los analisis estadisticos de un datastream en formato JSOn en la base de datos como se ha excplicaod antes.

En cuanto a consideraciones de seguridad y monitoreo, se ha hecho uso de loggers que permiten a los usuarios tener una idea de las acciones que lleva a cabo el servicio ademas de loggear las posibles excepciones que puedan tener lugar y que son taratadas a traves de estrucvturas try-catch.


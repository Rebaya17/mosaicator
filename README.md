# Mosaicator
_Cuarta asignación de Procesamiento Digital de Imágenes (UCV) 2-2017_

Se desea que Ud. desarrolle una aplicación con GUI en Java (NetBeans) y JavaCV, que permita construir una imagen de efecto
mosaico compuesta a partir de otras imágenes. Para esto se utilizará como fuente un video (con la ayuda del paquete FFmpeg
de JavaCV*) y siguiendo los siguientes lineamientos generales:

- Se debe proveer una interfaz para la captura y/o selección de un frame específico dentro del video, el cual
representará la imagen de entrada a la cual se le aplicará el efecto de fotomosaico.
- La imagen de entrada debe ser subdividida en NxM celdas de tamaño configurable.
- Cada celda debe ser comparada con una Base de Datos (BD) de imágenes en base a su color (utilizando tanto distancia
Euclidiana como el DeltaE de CIELab).
- La BD debe ser construida muestreando una cantidad representativa de frames dentro del mismo video y debe
indexarse en base a algún mecanismo como un promedio de color simple, ponderado o con super-muestreo. Es
importante que las imágenes de la BD sean seleccionadas de forma tal que cubran un rango significativo de colores
dentro del espectro para garantizar un buen contraste final.
- La BD debe poder explorarse mediante algún mecanismo que permita verificar las imágenes seleccionadas.
- La imagen resultante será la composición de las imágenes seleccionadas, cuyo tamaño estará supeditado a las
dimensiones de la celda.
- El tamaño final de la imagen resultante puede ser más grande que el de la imagen de entrada, en función de una
opción provista al usuario.


**Condiciones:**
- Se dará un porcentaje adicional sobre la nota final de la materia (a ser definido) a la(s) tarea(s) con mejor calidad,
nivel de contraste y definición de la imagen mosaico obtenida.
- Serán penalizadas las aplicaciones que no cumplan un mínimo estándar de usabilidad. Por ejemplo, que requieran
ser reiniciadas constantemente para probar algún requerimiento, que lean e impriman datos por consola como parte
de la interfaz, etc.
- Está prohibido emplear cualquier librería que no forme parte del core de Java o de JavaCV para el cumplimiento de
las funcionalidades solicitadas en esta tarea.
- La tarea puede ser realizada por equipo de dos (2) personas. Serán penalizadas con cero (0) puntos las copias
detectadas entre equipos o integrantes distintos.
- Se debe entregar un archivo zip con el proyecto NetBeans sin errores de compilación y ejecutable. El archivo debe
ser nombrado utilizando cédula y nombre del(los) integrante(s) (por ejemplo: “Tarea1-123456-PedroPerez-789012-
JuanLopez.zip”) y enviado al correo pdiucv@gmail.com.
- Cualquier aclaratoria relacionada con la tarea debe ser colocada en un archivo llamado “README.txt” dentro de la
carpeta del proyecto NetBeans.
- Entrega el día domingo 10/06/2018 hasta las 11:59:59 pm (GMT-4).



---
# Implementación
Se intenta seguir el patrón de diseño MVC con las siguientes consideraciones

- La clase **Mosaicator** es la clase principal del programa. Instancia todas las vistas, modelos, controladores e
inicia el programa.
- La clase **Mosaic** funge de modelo **almacenando toda la información** del mosaico como la ruta del video actual,
tamaño del mosaico, número de divisiones, ruta para exportar, lista de piezas del mosaico y cualquier otro miembro
importante, además se **implementan todos los métodos** que operan sobre el mosaico y el video.
- La clase **MainWindow** es la ventana principal de la aplicación. Contiene los elementos de la interfáz gráfica y sus
métodos sólo consisten en solicitar información de ellos o actualizarlos. Se han implementado los siguientes métodos
necesarios para el correcto funcionamiento del patrón de diseño:
  - **setupComponents**: inicializa los componentes asignando valores que no se modificarán durante la vida de la
  aplicación. La función más importante que realiza es asignar los **_ActionCommand_** a los componentes que generan
  eventos ya que estos se usan en la clase **Controller** para identificar qué componente se ha activado. Los
  **_ActionCommand_** son tomados del atributo **name** del mismo componente. Este valor se ha asignado desde el
  editor de interfaz gráfica GUI Builder de NetBeans.
  - **setController**: asigna a cada componente el controlador como **ActionListener** para que éste reciba todos los
  eventos y los implemente.
- La clase **Controller** exiende de **WindowAdapter** para capturar el evento de cierre de la ventana principal e
implementa las interfaces **ActionListener** y **ChangeListener** que capturan los eventos generados por los componentes
de las vistas. Dentro de sus miembros se encuentran las vistas y modelos de la aplicación y usa sus métodos para
conectar las clases solicitando, asignando y actualizando los datos necesarios. Tiene la tarea de inicializar la
aplicación luego de asignar todas las vistas y modelos con sus respectivos setters para posteriormente llamar al método
**initMVC** que asigna los valores por defectos de los componentes. Una vez hecho esto el método **actionPerformed** y
**stateChanged** están listos para capturar y ejecutar los eventos generados por cada componente.
**

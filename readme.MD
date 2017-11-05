urlreader
=========

Descripción
-----------

Este desarrollo escanea las páginas web a partir de una URL dada y recoge todas las páginas web asociadas, los enlaces y ficheros de cada una de ellas. Toda la información se guarda en base de datos.

Estructura
----------

* Proyecto: cada ejecución del programa.
	* Site: páginas web asociadas a la página web principal
		* Content: contenido de cada página web. Esta se compone de: 
			* Ficheros asociadas
			* Enlaces a otras páginas

Ejecutar
--------

java -jar urlreader.jar <url> <ruta de descarga de ficheros>

p.e.

java -jar urlreader.jar http://www.aecoc.es c:\\datos\\



Librerias utilizadas
--------------------

* jsoup-1.10.3.jar: 
* sqlite-jdbc-3.20.0.jar


Otras observaciones
-------------------

Hay que tener cuidado al ejecutarla ya que el sistema cuando encuentra un link a otra página se va para allí y empieza a escanear la página.



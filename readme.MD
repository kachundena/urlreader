urlreader
=========

Descripci�n
-----------

Este desarrollo escanea las p�ginas web a partir de una URL dada y recoge todas las p�ginas web asociadas, los enlaces y ficheros de cada una de ellas. Toda la informaci�n se guarda en base de datos.

Estructura
----------

* Proyecto: cada ejecuci�n del programa.
	* Site: p�ginas web asociadas a la p�gina web principal
		* Content: contenido de cada p�gina web. Esta se compone de: 
			* Ficheros asociadas
			* Enlaces a otras p�ginas

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

Hay que tener cuidado al ejecutarla ya que el sistema cuando encuentra un link a otra p�gina se va para all� y empieza a escanear la p�gina.



# Challenge LiterAlura

Este repositorio contiene la solución al Challenge LiterAlura, parte de la formación Backend de Alura Latam y Oracle.
💻 Descripción de la aplicación

La aplicación permite consultar libros y autores mediante la API de Gutendex. Los usuarios pueden interactuar con un menú que ofrece diversas opciones para consultar información sobre libros y autores. Cada libro consultado se guarda en una base de datos local, almacenando detalles tanto del libro como del autor. Además, se pueden realizar consultas a la base de datos para obtener la información almacenada.
👨‍💻 Desarrollado por

Fernando Daniel Tomé Derrigo
🛠️ Tecnologías utilizadas

    Java: Lenguaje principal para el desarrollo de la aplicación.
    Spring Boot: Framework utilizado para crear la estructura del proyecto y gestionar las dependencias.
    Spring Data JPA: Herramienta para interactuar con la base de datos de manera eficiente.
    PostgreSQL: Base de datos relacional donde se almacenan los libros y autores.

🚀 Cómo ejecutar el proyecto

    Clona este repositorio:

    git clone <repositorio_url>

    Asegúrate de tener Java y PostgreSQL instalados en tu máquina.

    Crea una base de datos en PostgreSQL para la aplicación.

    Configura las credenciales de la base de datos en el archivo application.properties de Spring Boot.

    Ejecuta el proyecto:

    mvn spring-boot:run

    Accede a la aplicación a través de la URL proporcionada por Spring Boot (por lo general, http://localhost:8080).

🛠️ Funcionalidades

    Consultar libros y autores desde la API de Gutendex.
    Almacenar la información de los libros y autores en una base de datos local (PostgreSQL).
    Consultar los libros y autores guardados en la base de datos.

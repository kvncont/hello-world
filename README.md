# hello-world
Este repositorio contiene una demo para mostrar como puede ser la integración y despliegue continuo de una aplicación en java (spring-boot) utilizando las siguientes tecnologías docker, helm y kubernetes. La idea es poder construir y desplegar la aplicación en un cluster de kubernetes con Jenkins o Azure Pipelines.

Este proyecto usa [Jib](https://github.com/GoogleContainerTools/jib) para construir automaticamente la imagen de docker que será instalada en el cluster. Si quieres visualizar el contenido de la imagen ejecuta los siguientes comandos.

Contruye la imagen de docker localmente (`kvncont/hello-world:latest`).
```
mvn compile jib:dockerBuild
```
Visualiza el contenido de la imagen (Necesitas tener docker instalado).
```
docker run --rm -it -v /var/run/docker.sock:/var/run/docker.sock wagoodman/dive:latest kvncont/hello-world:latest
```
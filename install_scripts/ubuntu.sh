#!/bin/bash

# Atualiza a lista de pacotes
apt update

# Instala OpenJDK 17
apt install -y openjdk-17-jdk maven python3 python3-pip python3-poetry pipx nodejs npm apt-transport-https ca-certificates curl software-properties-common nginx

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
apt update
apt install -y docker-ce

# Instala Jest globalmente
npm install -g jest

# Instala o mkdocs globalmente
pipx install mkdocs --include-deps --force
pipx inject mkdocs mkdocs-material mkdocs-simple-plugin plantuml-markdown mkdocs-mermaid2-plugin mkdocs_puml mkdocs-video --include-deps --force
pipx ensurepath
source ~/.bashrc

# Exibe as versões instaladas para verificação
echo "OpenJDK version:"
java -version

echo "Python version:"
python3 --version

echo "MkDocs version"
mkdocs --version

echo "Node.js version:"
node --version

echo "Docker version:"
docker --version

echo "Maven version:"
mvn --version

echo "Jest version:"
jest --version

echo "Poetry version:"
poetry --version

echo "Instalação concluída!"
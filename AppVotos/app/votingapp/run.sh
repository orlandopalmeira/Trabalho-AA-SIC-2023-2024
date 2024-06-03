#!/bin/bash

#* Obtém a directoria da script
get_script_path() {
    # Usa o caminho desta script para determinar a directoria atual
    echo "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
}

#* Obtém a directoria do repositório do trabalho
get_repo_path() {
    REPO_NAME='Trabalho-AA-SIC-2023-2024'
    current_path=$(get_script_path)
    
    # Encontra a directoria do repositório
    repo_path="${current_path%$REPO_NAME*}$REPO_NAME"
    
    echo "$repo_path"
}

repo_path=$(get_repo_path)
pom_xml="${repo_path}/AppVotos/app/votingapp/pom.xml"


#* Compilar o projecto
mvn clean
mvn -f $pom_xml package &&

#* Executar o projecto
java -jar "${repo_path}/AppVotos/app/votingapp/target/votingapp-0.0.1-SNAPSHOT.war" "$@"

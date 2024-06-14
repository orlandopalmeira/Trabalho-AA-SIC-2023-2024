#!/bin/bash

# Function to handle y/n prompt
ask_yes_no() {
    while true; do
        read -p "$1 [y/n]: " choice
        case "$choice" in
            y|Y ) return 0;;
            n|N ) return 1;;
            * ) echo "Responder apenas y/Y ou n/N";;
        esac
    done
}

# Verifica se o locust está instalado
if command -v locust &> /dev/null
then
    echo "O locust está instalado."
    echo "Adiante..."
else
    echo "O locust não está instalado."
    if ask_yes_no "Posso instalar o locust?"
    then
        echo "A instalar o locust (pip install locust)..."
        if pip install locust &> /dev/null;
        then
            echo "Locust instalado com sucesso."
        else
            echo "Erro ao instalar o locust."
            exit 1
        fi
    else
        echo "Instalação do locust cancelada."
        exit 1
    fi
fi

echo "A iniciar o locust..."

echo "Aceder a http://localhost:8089 para aceder ao locust"
locust -f workload.py --host=http://localhost:8080

# locust -f workload.py -u 50 -r 10 --host=http://localhost:8080 --headless
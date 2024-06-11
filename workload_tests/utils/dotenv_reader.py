import os

def get_current_path():
    """Obtém a directoria actual"""
    return os.path.dirname(os.path.abspath(__file__))

def get_repo_path():
    """Obtém a directoria do repositório do trabalho"""
    REPO_NAME = 'Trabalho-AA-SIC-2023-2024'
    path = get_current_path().split(REPO_NAME)[0] + REPO_NAME
    return path

def check_file_exists(file_path):
    """Verifica se um ficheiro existe"""
    return os.path.isfile(file_path)

class DotenvReader:
    """
    Classe para ler variáveis de ambiente de um ficheiro .env
    """
    def __init__(self, filepath:str = None):
        if not filepath:
            filepath = get_repo_path() + '/workload_tests/.env'
        if not check_file_exists(filepath):
            print(f"Não foi possível encontrar o ficheiro .env em '{filepath}'")
            exit(1)
        self.filepath = filepath
        self.env_vars = {}
        self.load()

    def load(self):
        with open(self.filepath, 'r') as file:
            for line in file:
                line = line.strip()
                if line and not line.startswith('#'):
                    key, value = line.split('=')
                    self.env_vars[key.strip()] = value.strip()

    def get(self, key):
        return self.env_vars.get(key)

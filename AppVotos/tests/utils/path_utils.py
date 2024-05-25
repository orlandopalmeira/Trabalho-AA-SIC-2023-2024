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
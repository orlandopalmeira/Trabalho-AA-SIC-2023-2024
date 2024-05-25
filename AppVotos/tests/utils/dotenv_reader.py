import utils.path_utils as pu

class DotenvReader:
    """Classe para ler variáveis de ambiente de um ficheiro .env
        1. Criar um ficheiro .env na directoria Trabalho-AA-SIC-2023-2024/AppVotos/tests
        2. O formato necessário do ficheiro .env está no exemplo Trabalho-AA-SIC-2023-2024/AppVotos/tests/.env_example
        3. Pode ser fornecida uma path no constructor desta classe para um ficheiro .env diferente noutra directoria diferente da default, mas não é recomendado
        4. O ficheiro .env está na .gitignore pelo que cada um deve ter o seu próprio ficheiro .env
    """
    def __init__(self, filepath:str = None):
        if not filepath:
            filepath = pu.get_repo_path() + '/AppVotos/tests/.env'
        if not pu.check_file_exists(filepath):
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

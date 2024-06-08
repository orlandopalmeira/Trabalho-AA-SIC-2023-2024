import os
import re
import javalang # pip install javalang

import utils.path_utils as pu

def find_java_files(directory):
    java_files = []
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith('.java'):
                java_files.append(os.path.join(root, file))
    return java_files

def parse_java_file(file_path):
    with open(file_path, 'r') as file:
        content = file.read()
    tree = javalang.parse.parse(content)
    return tree

def get_public_methods(tree):
    public_methods = {}
    for _, node in tree.filter(javalang.tree.MethodDeclaration):
        if 'public' in node.modifiers:
            public_methods[node.name] = node.position
    return public_methods

def find_method_usages(directory, method_name):
    usages = []
    java_files = find_java_files(directory)
    method_pattern = re.compile(r'\b{}\b'.format(re.escape(method_name)))
    for file_path in java_files:
        with open(file_path, 'r') as file:
            content = file.read()
            if method_pattern.search(content):
                usages.append(file_path)
    return usages

def find_unused_public_methods(directory):
    java_files = find_java_files(directory)
    unused_methods = []

    for file_path in java_files:
        tree = parse_java_file(file_path)
        public_methods = get_public_methods(tree)
        for method_name, position in public_methods.items():
            usages = find_method_usages(directory, method_name)
            if len(usages) == 1 and usages[0] == file_path:
                unused_methods.append((file_path, method_name, position))
    
    return unused_methods

def main(directory):
    unused_methods = find_unused_public_methods(directory)
    if unused_methods:
        print("Unused public methods found:")
        for file_path, method_name, position in unused_methods:
            file_path = file_path.split("votingapp")[2]
            print(f"Class file: {file_path}, Method: {method_name}, Line: {position[0]}")
    else:
        print("No unused public methods found.")

if __name__ == "__main__":
    project_directory = pu.get_repo_path() + '/AppVotos/app/votingapp/src/main/java'
    main(project_directory)

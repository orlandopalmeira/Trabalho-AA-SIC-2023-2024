import csv

# function that converts a csv file to a list of dictionaries
def csv_to_dict_list(file_path: str) -> list[dict]:
    """Converts a csv file to a list of dictionaries"""
    with open(file_path, 'r') as f:
        reader = csv.DictReader(f)
        return [dict(row) for row in reader]
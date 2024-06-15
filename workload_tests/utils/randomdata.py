import random, os, time
from datetime import datetime, timedelta

def gen_number():
    rand_num = random.randint(1, 10_000)
    ts = int(time.time())
    return f"{rand_num}-{ts}"


def get_sample_user():
    """Returns a random user"""
    number = gen_number()
    names = ['Alice', 'Bob', 'Charlie', 'David', 'Eve', 'Frank', 'Grace', 'Hank', 'Ivy', 'Jack', 'Kate', 'Liam', 'Mia', 'Noah', 'Olivia', 'Peter', 'Quinn', 'Rose', 'Sam', 'Tina', 'Uma', 'Vince', 'Wendy', 'Xander', 'Yara', 'Zack']
    surnames = ['Smith', 'Johnson', 'Williams', 'Jones', 'Brown', 'Davis', 'Miller', 'Wilson', 'Moore', 'Taylor', 'Anderson', 'Thomas', 'Jackson', 'White', 'Harris', 'Martin', 'Thompson', 'Garcia', 'Martinez', 'Robinson', 'Clark', 'Rodriguez', 'Lewis', 'Lee', 'Walker']
    name = random.choice(names)
    surname = random.choice(surnames)
    final_name = name + ' ' + surname
    email = f'{name.lower()}.{surname.lower()}_{number}@e.c'

    return {
        "name": final_name,
        "email": email,
        "password": f"user_{number}",
    }

def get_sample_users(quantity: int):
    """Returns a list of random users"""
    return [get_sample_user() for _ in range(quantity)]


def get_random_image(folder_path = "images/"):
    """
    Get a random image file from a specified folder.

    Parameters:
    folder_path (str): The path to the folder containing images.

    Returns:
    str: The path to the randomly selected image.
    """
    # List all files in the folder
    files = os.listdir(folder_path)

    # Filter the list to include only image files
    image_extensions = ['.png', '.jpg', '.jpeg', '.gif', '.bmp', '.tiff']
    images = [file for file in files if os.path.splitext(file)[1].lower() in image_extensions]

    if not images:
        raise ValueError("No image files found in the specified folder.")

    # Select a random image from the list
    random_image = random.choice(images)

    # Return the full path to the random image
    return os.path.join(folder_path, random_image)


if __name__ == "__main__":

    for i in range(10):
        image_path = get_random_image("../images/")
        print(image_path)
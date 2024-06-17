from google.cloud import storage

def delete_all_files_in_bucket(bucket_name):
    
    service_account_file_path = "/home/pedro/Trabalho-AA-SIC-2023-2024/AppVotos/app/votingapp/src/main/resources/aa-sic-424610-eea4d305377e.json"
    storage_client = storage.Client.from_service_account_json(service_account_file_path)
    
    # Get the bucket
    bucket = storage_client.bucket(bucket_name)
    
    # List all objects in the bucket
    blobs = bucket.list_blobs()
    
    # Delete all objects
    for blob in blobs:
        blob.delete()
        print(f'Blob {blob.name} deleted.')

if __name__ == "__main__":
    bucket_name = "images-aa-sic"
    delete_all_files_in_bucket(bucket_name)

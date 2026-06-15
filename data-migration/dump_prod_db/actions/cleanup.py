import os
import logging

def cleanup_temp_files(temp_dir):
    '''Remove temporary files created during the dump and restore processes.'''
    try:
        for filename in os.listdir(temp_dir):
            file_path = os.path.join(temp_dir, filename)
            if os.path.isfile(file_path):
                os.remove(file_path)
                logging.info(f"Removed temporary file: {file_path}")
    except Exception as e:
        logging.error(f"Error occurred while cleaning up temporary files: {e}")
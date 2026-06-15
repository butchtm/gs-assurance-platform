import argparse
import logging
from actions.dump import dump_database as perform_dump
from actions.restore import restore_database as perform_restore
from actions.clear import clear_target_database_standalone as perform_clear

def main():
    parser = argparse.ArgumentParser(description="Database Dump and Restore Utility")
    parser.add_argument(
        'action',
        choices=['dump', 'restore', 'clear', 'backup-users', 'merge-users', 'backup-accounts', 'merge-accounts', 'obfuscate'],
        help="Operation: dump, restore, clear, backup-users (pre-restore), merge-users (post-restore), backup-accounts (pre-restore), merge-accounts (post-restore), obfuscate (post-backup, pre-merge)."
    )
    parser.add_argument(
        '--clear-target',
        action='store_true',
        help="Clear the target database before restoring."
    )
    parser.add_argument(
        '--dump-file',
        help="Path to dump file to restore from (defaults to $DUMP_FILE or 'dump_file.dump')."
    )
    
    args = parser.parse_args()

    logging.basicConfig(level=logging.INFO)

    if args.action == 'dump':
        logging.info("Starting database dump...")
       
        perform_dump()
        logging.info("Database dump completed successfully.")
        
    elif args.action == 'restore':
        logging.info("Starting database restore (will backup and merge target users and accounts)...")
        perform_restore(dump_file=args.dump_file, clear_target=args.clear_target)
        logging.info("Database restore (with user and account merge) completed successfully.")
    elif args.action == 'clear':
        logging.info("Starting database clear...")
        perform_clear()
        logging.info("Database clear completed successfully.")

if __name__ == "__main__":
    main()
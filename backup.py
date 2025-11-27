
import os
import subprocess
import sys
from datetime import datetime
from dotenv import load_dotenv


# Carrega as variáveis do .env.
load_dotenv()

# Configurações do Docker
CONTAINER_NAME = os.getenv("CONTAINER_NAME", "timescaledb")

# Configurações do Postgres
DB_USER = os.getenv("POSTGRES_USER")
DB_NAME = os.getenv("POSTGRES_DB")

# Configurações de Diretórios e Backup
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
BACKUP_DIR = os.getenv("LOCAL_BACKUP_DIR", os.path.join(BASE_DIR, "backups"))

GDRIVE_DIR = os.getenv("GDRIVE_DIR", "Backup_Seeds_Lab")
REMOTE_DRIVE = f"gdrive:{GDRIVE_DIR}"
DAYS_TO_KEEP = int(os.getenv("DAYS_TO_KEEP", 7))


def log(message):
    """Função simples de log com timestamp"""
    print(f"[{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}] {message}")


def run_command(cmd):
    """Executa comando no shell e retorna True se sucesso"""
    try:
        # shell=True permite usar pipes (|) e redirecionadores (>)
        subprocess.run(cmd, shell=True, check=True)
        return True
    except subprocess.CalledProcessError as e:
        log(f"ERRO ao executar: {cmd}")
        log(f"Detalhe: {e}")
        return False


def main():
    """Start Backup."""
    log("--- INICIANDO ROTINA DE BACKUP ---")

    # 1. Cria diretório se não existir
    if not os.path.exists(BACKUP_DIR):
        log(f"Criando diretório local: {BACKUP_DIR}")
        os.makedirs(BACKUP_DIR)

    now = datetime.now()
    timestamp = now.strftime('%Y-%m-%d_%H-%M-%S')

    # Define prefixo (Weekly no Domingo, Daily nos outros dias)
    # 6 = Domingo na lib datetime
    prefix = "WEEKLY" if now.weekday() == 6 else "DAILY"

    filename = f"{prefix}_{timestamp}.sql.gz"
    filepath = os.path.join(BACKUP_DIR, filename)

    log(f"Arquivo alvo: {filename}")

    # 2. Executa pg_dump via Docker
    log(f"Executando dump no container '{CONTAINER_NAME}'...")
    dump_cmd = f"docker exec {CONTAINER_NAME} pg_dump -U {DB_USER} {DB_NAME} | gzip > {filepath}"

    if run_command(dump_cmd):
        log("Backup local criado com sucesso.")

        # 3. Envia para o Google Drive com Rclone
        log(f"Enviando para o Google Drive ({REMOTE_DRIVE})...")
        rclone_cmd = f"rclone copy {filepath} {REMOTE_DRIVE}"

        if run_command(rclone_cmd):
            log("Upload concluído!")
        else:
            log("FALHA CRÍTICA no upload para o Drive.")

        # 4. Limpeza Local
        log(f"Limpando arquivos locais com mais de {DAYS_TO_KEEP} dias...")
        clean_cmd = f"find {BACKUP_DIR} -name '*.sql.gz' -mtime +{DAYS_TO_KEEP} -delete"
        run_command(clean_cmd)

    else:
        log("FALHA CRÍTICA: Não foi possível criar o dump do banco.")

    log("--- FIM DA ROTINA ---")


if __name__ == "__main__":
    """Start Backup."""
    main()
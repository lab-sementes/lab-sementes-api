# Configurando o Agendamento de Backups

Para que o Linux realize o processo com regularidade, automaticamente, vamos usar o Cron:

```commandline
crontab -e
```

E adicione o trecho abaixo para fazer o script rodar sozinho todo dia às 02:00 da manhã:

```crontab
# Backup TimescaleDB - Todo dia as 02:00
0 2 * * * /usr/bin/python3 /lab-sementes-api/backup.py >> /backups-banco/backup.log 2>&1
```

### Importante
> O Cron não carrega o PATH do usuário, então rode `which python3` para descobrir o caminho absoluto.
 
> Se o backup falhar, o Cron irá gerar um arquivo `backup.log`.

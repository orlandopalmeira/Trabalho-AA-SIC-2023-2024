FROM mysql:8.3.0
# Para criar uma imagem de docker que já vem com uma base de dados com as tabelas direitinhas

COPY init.sql /docker-entrypoint-initdb.d/

# Defina as variáveis de ambiente para o MySQL
ENV MYSQL_ROOT_PASSWORD=mysql
ENV MYSQL_DATABASE=votingapp

# Exponha a porta 3306
EXPOSE 3306
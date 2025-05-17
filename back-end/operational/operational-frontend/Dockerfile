# Estágio de construção
FROM node:16-alpine as build

WORKDIR /app

# Copiar package.json e package-lock.json
COPY package*.json ./

# Instalar dependências
RUN npm ci

# Copiar restante do código
COPY . .

# Construir o aplicativo
RUN npm run build

# Estágio de produção
FROM nginx:stable-alpine

# Copiar a build do React para o diretório de servir do Nginx
COPY --from=build /app/build /usr/share/nginx/html

# Copiar configuração personalizada do Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expor a porta que o Nginx escuta
EXPOSE 80

# Iniciar o Nginx
CMD ["nginx", "-g", "daemon off;"]
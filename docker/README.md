# IST using DOCKER

To run IST using docker you will need to create the image.
``` 
docker build --tag ist-loader .
```

After having created the image you can run the docker compose file.  
This will run scalardl with IST contracts and functions.
```
docker compose up -d
```

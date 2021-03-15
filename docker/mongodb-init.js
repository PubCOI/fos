db.createUser({
    user: "fos-docker-runtime",
    pwd: "fos-docker-runtime-pass",
    roles: [
        {role: "readWrite", db: "docker-fos"}
    ]
})
;
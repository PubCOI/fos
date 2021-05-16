db.createUser({
    user: "fos-runtime",
    pwd: "fos-pass",
    roles: [
        {role: "readWrite", db: "fos-docker"}
    ]
})
;
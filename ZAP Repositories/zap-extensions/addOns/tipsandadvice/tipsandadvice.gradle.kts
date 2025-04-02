version = "0.0.1"
description = "Add-On for my Final Year Project"

zapAddOn {
    addOnName.set("Tips and Advice")

    manifest {
        author.set("Unais Qureshi")
    }
}

crowdin {
    configuration {
        val resourcesPath = "org/zaproxy/addon/${zapAddOn.addOnId.get()}/resources/"
        tokens.put("%messagesPath%", resourcesPath)
        tokens.put("%helpPath%", resourcesPath)
    }
}

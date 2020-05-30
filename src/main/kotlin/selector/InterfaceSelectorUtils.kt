fun getInterfacesRecursively(model: Model): List<Interface> =
    model.interfaces + model.sketchComponents.flatMap {
        getInterfacesRecursively(it.model)
    }

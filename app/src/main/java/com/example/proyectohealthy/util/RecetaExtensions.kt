package com.example.proyectohealthy.util

import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.local.entity.RecetaGuardada
import java.util.Date

fun RecetaGuardada.toMiAlimento(): MisAlimentos {
    return MisAlimentos(
        id = "",
        idPerfil = this.idPerfil,
        nombre = this.nombre,
        marca = "Receta propia",
        categoria = "Recetas",
        nombrePorcion = "plato",
        pesoPorcion = this.valoresNutricionales.porcion,
        unidadPorcion = this.valoresNutricionales.unidadPorcion,
        calorias = this.valoresNutricionales.calorias,
        proteinas = this.valoresNutricionales.proteinas,
        carbohidratos = this.valoresNutricionales.carbohidratos,
        grasas = this.valoresNutricionales.grasas,
        grasasSaturadas = this.valoresNutricionales.grasasSaturadas,
        grasasTrans = this.valoresNutricionales.grasasTrans,
        sodio = this.valoresNutricionales.sodio,
        fibra = this.valoresNutricionales.fibra,
        azucares = this.valoresNutricionales.azucares,
        diaCreado = Date()
    )
}
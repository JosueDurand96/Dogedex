package com.durand.dogedex.ui.user_fragment.register_can

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.durand.dogedex.BR
import com.durand.dogedex.data.dto.RegisterCanRequest

class RegisterCanForm : BaseObservable() {

    @Bindable
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @Bindable
    var date: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.date)
        }

    @Bindable
    var specie: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.specie)
        }

    @Bindable
    var gender: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.gender)
        }

    @Bindable
    var race: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.race)
        }

    @Bindable
    var size: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.size)
        }

    @Bindable
    var character: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.character)
        }

    @Bindable
    var color: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.color)
        }

    @Bindable
    var coat: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.coat)
        }

    @Bindable
    var sterilized: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.sterilized)
        }

    @Bindable
    var district: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.district)
        }

    @Bindable
    var obtainMode: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.obtainMode)
        }

    @Bindable
    var tenancyReason: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.tenancyReason)
        }

    fun validate(): Boolean {
        return character.isNotEmpty()
                && color.isNotEmpty()
                && district.isNotEmpty()
                && specie.isNotEmpty()
                && sterilized.isNotEmpty()
                && date.isNotEmpty()
                && gender.isNotEmpty()
                && obtainMode.isNotEmpty()
                && name.isNotEmpty()
                && coat.isNotEmpty()
                && tenancyReason.isNotEmpty()
                && size.isNotEmpty()
    }

    fun toDto(userId: Int ): RegisterCanRequest {
        return RegisterCanRequest(
            caracter = character,
            color = color,
            distrito = district,
            especie = specie,
            esterilizado = sterilized,
            fechaNacimiento = date,
            genero = gender,
            modoObtencion = obtainMode,
            nombre = name,
            pelaje = coat,
            razonTenencia = tenancyReason,
            tamano = size,
            idUsuario = userId,
            idRaza = 28
        )
    }


}
package com.mf.weatherapp.data.models

class UiStates(var networkReqStates: NetworkReqStates, var errorMessage: String?){

}
enum class NetworkReqStates {
    EMPTY,REQUESTED, LOADING, ERROR, SUCCESS
}
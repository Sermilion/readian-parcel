package net.readian.parcel.di.qualifiers

import javax.inject.Qualifier

@Qualifier
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class Authenticated

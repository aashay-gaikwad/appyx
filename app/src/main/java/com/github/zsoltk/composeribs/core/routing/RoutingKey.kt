package com.github.zsoltk.composeribs.core.routing

import android.os.Parcelable

interface RoutingKey<Key> : Parcelable {
    val routing: Key
}

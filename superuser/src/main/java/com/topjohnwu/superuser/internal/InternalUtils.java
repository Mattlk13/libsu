/*
 * Copyright 2018 John "topjohnwu" Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.topjohnwu.superuser.internal;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import com.topjohnwu.superuser.Shell;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class InternalUtils {

    private static Field mBaseContext;
    private static Method currentApplication;

    static {
        try {
            mBaseContext = ContextWrapper.class.getDeclaredField("mBase");
            mBaseContext.setAccessible(true);
            currentApplication = Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication");
        } catch (NoSuchFieldException e) {
            /* Impossible */
        } catch (ClassNotFoundException e) {
            /* Impossible */
        } catch (NoSuchMethodException e) {
            /* Impossible */
        }
    }

    static void log(String tag, Object log) {
        if (hasFlag(Shell.FLAG_VERBOSE_LOGGING))
            Log.d(tag, log.toString());
    }

    public static void stackTrace(Throwable t) {
        if (hasFlag(Shell.FLAG_VERBOSE_LOGGING))
            Log.d("LIBSU", "Internal Error", t);
    }

    public static boolean hasFlag(int flags) {
        return hasFlag(Shell.Config.getFlags(), flags);
    }

    public static boolean hasFlag(int base, int flags) {
        return (base & flags) == flags;
    }

    public static ContextWrapper getContext() {
        try {
            return (ContextWrapper) currentApplication.invoke(null);
        } catch (Exception e) {
            return null;
        }
    }

    static void replaceBaseContext(ContextWrapper wrapper, Context base) {
        try {
            mBaseContext.set(wrapper, base);
        } catch (IllegalAccessException e) {
            /* Impossible */
        }
    }
}

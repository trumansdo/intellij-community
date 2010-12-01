/*
 * Copyright 2000-2010 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.codeInspection.internal;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.util.ArrayUtil;

public class InternalInspectionToolsProvider implements InspectionToolProvider {
  public static final String GROUP_NAME = "IDEA Platform Inspections";
  private static final Class[] CLASSES = new Class[] {
    UndesirableClassUsageInspection.class,
    GtkPreferredJComboBoxRendererInspection_.class
  };

  @Override
  public Class[] getInspectionClasses() {
    if (!isActive()) return ArrayUtil.EMPTY_CLASS_ARRAY;
    return CLASSES;
  }

  public static Class[] getPublicClasses() {
    if (isActive()) return ArrayUtil.EMPTY_CLASS_ARRAY;
    return CLASSES;
  }

  private static boolean isActive() {
    return ApplicationManagerEx.getApplicationEx().isInternal();
  }
}

/*
 * Copyright 2000-2016 JetBrains s.r.o.
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
package com.intellij.openapi.fileEditor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.EditorBundle;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.util.Conditions.or;

public class PsiElementNavigatable implements Navigatable {
  private final SmartPsiElementPointer<PsiElement> myPointer;

  public PsiElementNavigatable(@NotNull PsiElement element) {
    myPointer = SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element);
  }

  @Override
  public final void navigate(boolean requestFocus) {
    PsiElement originalElement = myPointer.getElement();
    if (originalElement != null && originalElement.isValid()) {
      PsiElement element = ObjectUtils.notNull(originalElement.getNavigationElement(), originalElement);
      VirtualFile file = element.getContainingFile().getVirtualFile();
      if (file != null) {
        new Task.Modal(element.getProject(), EditorBundle.message("editor.open.file.progress", file.getName()), true) {
          @Override
          public void run(@NotNull ProgressIndicator indicator) {
            int offset = ReadAction.compute(() -> element.isValid() ? element.getTextOffset() : -1);  // may trigger decompilation
            indicator.checkCanceled();
            if (offset >= 0) {
              OpenFileDescriptor descriptor = new OpenFileDescriptor(myProject, file, offset);
              Condition<?> expired = or(myProject.getDisposed(), o -> !file.isValid());
              ApplicationManager.getApplication().invokeLater(() -> descriptor.navigate(requestFocus), expired);
            }
          }
        }.queue();
      }
    }
  }

  @Override
  public boolean canNavigate() {
    PsiElement element = myPointer.getElement();
    return element != null && element.isValid() && ObjectUtils.notNull(element.getNavigationElement(), element).getContainingFile().getVirtualFile() != null;
  }

  @Override
  public boolean canNavigateToSource() {
    return canNavigate();
  }
}
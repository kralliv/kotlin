// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.ui.tabs;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.ui.FileColorManager;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

@State(name="SharedFileColors", storages = @Storage("fileColors.xml"))
final class FileColorProjectLevelConfigurationManager implements PersistentStateComponent<Element> {
  private final Project myProject;

  FileColorProjectLevelConfigurationManager(@NotNull Project project) {
    myProject = project;
  }

  @Override
  public Element getState() {
    return ((FileColorManagerImpl)FileColorManager.getInstance(myProject)).getModel().save(true);
  }

  @Override
  public void loadState(@NotNull Element state) {
    ((FileColorManagerImpl)FileColorManager.getInstance(myProject)).getModel().load(state, true);
  }
}

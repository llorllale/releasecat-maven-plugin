/*
 * Copyright 2018 George Aristy
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

package org.llorllale.mvn.plgn.releasecat;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Uploads releases to GitHub.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @since 0.1.0
 */
@Mojo(name = "upload", defaultPhase = LifecyclePhase.DEPLOY)
public final class Upload extends AbstractMojo {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}

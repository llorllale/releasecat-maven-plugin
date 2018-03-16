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

import java.io.File;

/**
 * A release asset.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @since 0.4.0
 */
public final class Asset {
  private String name;
  private String type;
  private File file;

  /**
   * Ctor.
   * 
   * @since 0.4.0
   */
  public Asset() {
    //intentional
  }

  /**
   * For testing purposes.
   * 
   * @param name see {@link #getName()}
   * @param type see {@link #getType()}
   * @param file see {@link #getFile()}
   */
  public Asset(String name, String type, File file) {
    this.name = name;
    this.type = type;
    this.file = file;
  }

  /**
   * The display name for the asset once uploaded.
   * 
   * @return the display name for the asset once uploaded
   * @since 0.4.0
   */
  public String getName() {
    return this.name;
  }

  /**
   * The asset's content type (eg. {@code text/xml}).
   * 
   * @return the asset's content type
   * @since 0.4.0
   */
  public String getType() {
    return this.type;
  }

  /**
   * The asset's local path.
   * 
   * @return the asset's local path
   * @since 0.4.0
   */
  public File getFile() {
    return this.file;
  }
}

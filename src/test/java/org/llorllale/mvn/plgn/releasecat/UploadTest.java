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

// @checkstyle AvoidStaticImport (3 lines)
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.jcabi.github.Release;
import com.jcabi.github.Releases;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.mock.MkGithub;
import java.io.IOException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

/**
 * Tests for {@link Upload}.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @since 0.1.0
 * @checkstyle MethodName (500 lines)
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public final class UploadTest {
  /**
   * Release should be created with the given tag.
   * 
   * @throws Exception unexpected
   * @since 0.1.0
   */
  @Test
  public void releaseIsCreated() throws Exception {
    final Repo repo = new MkGithub().repos()
      .create(new Repos.RepoCreate("my_user/my_project", false));
    new Upload("Tag v1.0", "Name v1.0", () -> repo).execute();
    assertNotNull(
      new Releases.Smart(repo.releases()).find("Tag v1.0")
    );
  }

  /**
   * Release should be created with the given name.
   * 
   * @throws Exception unexpected
   * @since 0.1.0
   */
  @Test
  public void releaseHasGivenName() throws Exception {
    final Repo repo = new MkGithub().repos()
      .create(new Repos.RepoCreate("my_user/my_project", false));
    new Upload("Tag v1.0", "Name v1.0", () -> repo).execute();
    assertThat(
      new Release.Smart(new Releases.Smart(repo.releases()).find("Tag v1.0")).name(),
      is("Name v1.0")
    );
  }

  /**
   * Mojo should wrap IOException in MojoFailureException.
   * 
   * @throws Exception MojoFailureException
   * @since 0.1.0
   */
  @Test(expected = MojoFailureException.class)
  public void mojoFailureIfIoError() throws Exception {
    new Upload(
      "my_user", "wrong_project",
      () -> {
        throw new IOException();
      }
    ).execute();
  }
}

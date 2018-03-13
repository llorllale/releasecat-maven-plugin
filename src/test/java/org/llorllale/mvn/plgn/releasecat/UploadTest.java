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

// @checkstyle AvoidStaticImport (4 lines)
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
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

  /**
   * Mojo should fail if the configuration is not specified (ie. is null). This results in an
   * internal IllegalArgumentException.
   * 
   * @throws Exception MojoFailureException
   * @since 0.1.0
   */
  @Test(expected = MojoFailureException.class)
  public void mojoFailureIfConfigNotSpecified() throws Exception {
    new Upload().execute();
  }

  /**
   * Release should be created with the given description.
   * 
   * @throws Exception unexpected
   * @since 0.2.0
   */
  @Test
  public void releaseHasGivenDescription() throws Exception {
    final Repo repo = new MkGithub().repos()
      .create(new Repos.RepoCreate("my_user/my_project", false));
    new Upload("Tag v1.0", "Name v1.0", "Description", () -> repo).execute();
    assertThat(
      new Release.Smart(new Releases.Smart(repo.releases()).find("Tag v1.0")).body(),
      is("Description")
    );
  }

  /**
   * Release should be created with the given description.
   * 
   * @throws Exception unexpected
   * @since 0.2.0
   * @todo #3:30min jcabi-github should not be prepopulating optional attributes of fake
   *  releases with default values like this. That's why this test is checking for
   *  'Description of the release'. I've opened https://github.com/jcabi/jcabi-github/issues/1362
   *  for this issue. When that is fixed, come back here and refactor this test.
   */
  @Test
  public void descriptionIsNullIfNotSpecified() throws Exception {
    final Repo repo = new MkGithub().repos()
      .create(new Repos.RepoCreate("my_user/my_project", false));
    new Upload("Tag v1.0", "Name v1.0", null, () -> repo).execute();
    assertThat(
      new Release.Smart(new Releases.Smart(repo.releases()).find("Tag v1.0")).body(),
      is("Description of the release")
    );
  }

  /**
   * Release should be marked as 'prerelease' if such is specified.
   * 
   * @throws Exception unexpected
   * @since 0.2.0
   * @todo #4:15min jcabi-github has a bug
   *  (https://github.com/jcabi/jcabi-github/issues/1363) that makes it impossible to mark
   *  a MkRelease as prerelease. When that is fixed, come back here and change 'assertFalse'
   *  for 'assertTrue' and make sure it works.
   */
  @Test
  public void prepreleaseTrue() throws Exception {
    final Repo repo = new MkGithub("my_user").repos()
      .create(new Repos.RepoCreate("my_project", false));
    new Upload("Tag v1.0", "Name v1.0", "", true, () -> repo).execute();
    assertFalse(
      new Release.Smart(new Releases.Smart(repo.releases()).find("Tag v1.0")).prerelease()
    );
  }

  /**
   * Release should not be marked as 'prerelease' if such is not specified.
   * 
   * @throws Exception unexpected
   * @since 0.2.0
   */
  @Test
  public void prepreleaseFalse() throws Exception {
    final Repo repo = new MkGithub().repos()
      .create(new Repos.RepoCreate("my_user/my_project", false));
    new Upload("Tag v1.0", "Name v1.0", "", false, () -> repo).execute();
    assertFalse(
      new Release.Smart(new Releases.Smart(repo.releases()).find("Tag v1.0")).prerelease()
    );
  }
}

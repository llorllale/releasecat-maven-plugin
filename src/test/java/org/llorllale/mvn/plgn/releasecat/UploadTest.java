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
import com.jcabi.github.ReleaseAsset;
import com.jcabi.github.Releases;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.mock.MkGithub;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.apache.maven.plugin.MojoFailureException;
import org.cactoos.io.InputOf;
import org.cactoos.io.LengthOf;
import org.cactoos.io.OutputTo;
import org.cactoos.io.TeeInput;
import org.cactoos.io.TempFile;
import org.cactoos.text.TextOf;
import org.junit.Test;

/**
 * Tests for {@link Upload}.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @since 0.1.0
 * @checkstyle MethodName (500 lines)
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MethodCount"})
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
   */
  @Test
  public void descriptionIsEmptyIfNotSpecified() throws Exception {
    final Repo repo = new MkGithub().repos()
      .create(new Repos.RepoCreate("my_user/my_project", false));
    new Upload("Tag v1.0", "Name v1.0", null, () -> repo).execute();
    assertThat(
      new Release.Smart(new Releases.Smart(repo.releases()).find("Tag v1.0")).body(),
      is("")
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

  /**
   * Release must have description text from the given file.
   * 
   * @throws Exception unexpected
   * @since 0.3.0
   */
  @Test
  public void releaseDescriptionFromFile() throws Exception {
    final File description = Files.createTempFile("", "").toFile();
    new LengthOf(
      new TeeInput(
        new InputOf("This is a test description from file."),
        new OutputTo(description)
      )
    ).value();
    final Repo repo = new MkGithub("my_user").repos()
      .create(new Repos.RepoCreate("my_project", false));
    new Upload("Tag v1.0", "Name v1.0", null, description, false, () -> repo).execute();
    assertThat(
      new Release.Smart(new Releases.Smart(repo.releases()).find("Tag v1.0")).body(),
      is(new TextOf(description).asString())
    );
  }

  /**
   * Release must have description text from the given file.
   * 
   * @throws Exception unexpected
   * @since 0.3.0
   */
  @Test
  public void descriptionConfigHasPrecedenceOverDescriptionFromFile() throws Exception {
    final File file = Files.createTempFile("", "").toFile();
    new LengthOf(
      new TeeInput(
        new InputOf("This is a test description from file."),
        new OutputTo(file)
      )
    ).value();
    final Repo repo = new MkGithub("my_user").repos()
      .create(new Repos.RepoCreate("my_project", false));
    new Upload(
      "Tag v1.0", "Name v1.0", "Inline description", file, false, () -> repo
    ).execute();
    assertThat(
      new Release.Smart(new Releases.Smart(repo.releases()).find("Tag v1.0")).body(),
      is("Inline description")
    );
  }

  /**
   * Asset must have the given name.
   * 
   * @throws Exception unexpected
   * @since 0.4.0
   */
  @Test
  public void uploadAssetsWithSpecifiedName() throws Exception {
    final File asset = Files.createTempFile("", "").toFile();
    new LengthOf(
      new TeeInput(
        new InputOf("This is a test asset."),
        new OutputTo(asset)
      )
    ).value();
    final List<Asset> assets = Arrays.asList(
      new Asset("test_asset.txt", "text/plain", asset)
    );
    final Repo repo = new MkGithub().repos()
      .create(new Repos.RepoCreate("my_user/my_project", false));
    new Upload(
      "Tag v1.0", "Name v1.0", "Inline description", null, false, assets, () -> repo
    ).execute();
    assertThat(
      new ReleaseAsset.Smart(
        new Releases.Smart(repo.releases()).find("Tag v1.0").assets().get(1)
      ).name(),
      is(assets.get(0).getName())
    );
  }

  /**
   * Asset must have the given content type.
   * 
   * @throws Exception unexpected
   * @since 0.4.0
   */
  @Test
  public void uploadAssetsWithSpecifiedType() throws Exception {
    final File asset = Files.createTempFile("", "").toFile();
    new LengthOf(
      new TeeInput(
        new InputOf("This is a test asset."),
        new OutputTo(asset)
      )
    ).value();
    final List<Asset> assets = Arrays.asList(
      new Asset("test_asset.txt", "text/plain", asset)
    );
    final Repo repo = new MkGithub().repos()
      .create(new Repos.RepoCreate("my_user/my_project", false));
    new Upload(
      "Tag v1.0", "Name v1.0", "Inline description", null, false, assets, () -> repo
    ).execute();
    assertThat(
      new ReleaseAsset.Smart(
        new Releases.Smart(repo.releases()).find("Tag v1.0").assets().get(1)
      ).contentType(),
      is(assets.get(0).getType())
    );
  }

  /**
   * Asset must have the same contents.
   * 
   * @throws Exception unexpected
   * @since 0.4.0
   */
  @Test
  public void uploadAssetsWithSpecifiedContents() throws Exception {
    try (TempFile asset = new TempFile()) {
      new LengthOf(
        new TeeInput(
          new InputOf("This is a test asset."),
          new OutputTo(asset.value())
        )
      ).value();
      final Repo repo = new MkGithub("my_user").repos()
        .create(new Repos.RepoCreate("my_project", false));
      new Upload(
        "Tag v1.0", "Name v1.0", "Inline description", null, false,
        Arrays.asList(new Asset("test_asset.txt", "text/plain", asset.value().toFile())),
        () -> repo
      ).execute();
      assertThat(
        new TextOf(
          DatatypeConverter.parseBase64Binary(
            new TextOf(
              new InputOf(
                new Releases.Smart(repo.releases()).find("Tag v1.0").assets().get(1).raw()
              )
            ).asString()
          )
        ).asString(),
        is(new TextOf(asset.value()).asString())
      );
    }
  }

  /**
   * Release should not be created with a name if it is not given.
   * 
   * @throws Exception unexpected
   * @since 0.1.0
   */
  @Test
  public void releaseHasNoNameIfNotProvided() throws Exception {
    final Repo repo = new MkGithub().repos()
      .create(new Repos.RepoCreate("my_user/my_project", false));
    new Upload("Tag v1.0", null, () -> repo).execute();
    assertThat(
      new Release.Smart(new Releases.Smart(repo.releases()).find("Tag v1.0")).name(),
      is("")
    );
  }
}

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

import com.jcabi.github.Coordinates;
import com.jcabi.github.Release;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.cactoos.Scalar;
import org.cactoos.io.BytesOf;
import org.cactoos.scalar.IoCheckedScalar;
import org.cactoos.text.TextOf;

/**
 * Uploads releases to GitHub.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @since 0.1.0
 */
@Mojo(name = "upload", defaultPhase = LifecyclePhase.DEPLOY)
public final class Upload extends AbstractMojo {
  @Parameter(name = "token", property = "releasecat.token", required = true)
  private String token;

  @Parameter(name = "user", property = "releasecat.user", required = true)
  private String user;

  @Parameter(name = "repo", property = "releasecat.repo", required = true)
  private String repo;

  @Parameter(name = "tag", property = "releasecat.tag", required = true)
  private String tag;

  @Parameter(name = "name", property = "releasecat.name")
  private String name;

  @Parameter(name = "description", property = "releasecat.description")
  private String description;

  @Parameter(name = "descriptionFromFile", property = "releasecat.descriptionFromFile")
  private File descriptionFromFile;

  @Parameter(name = "prerelease", property = "releasecat.prerelease", defaultValue = "false")
  private boolean prerelease;

  @Parameter(name = "assets")
  private List<Asset> assets;

  private final IoCheckedScalar<Repo> githubRepo;

  /**
   * Ctor.
   * 
   * @since 0.1.0
   */
  public Upload() {
    this.githubRepo = new IoCheckedScalar<>(
      () -> new RtGithub(this.token).repos().get(new Coordinates.Simple(this.user, this.repo))
    );
  }

  /**
   * For testing purposes.
   * 
   * @param tag the git tag
   * @param name the release name
   * @param repo the github repo
   * @since 0.1.0
   */
  Upload(String tag, String name, Scalar<Repo> repo) {
    this(tag, name, "", repo);
  }

  /**
   * For testing purposes.
   * 
   * @param tag the git tag
   * @param name the release name
   * @param description the release's description
   * @param repo the github repo
   * @since 0.2.0
   */
  Upload(String tag, String name, String description, Scalar<Repo> repo) {
    this(tag, name, description, false, repo);
  }

  /**
   * For testing purposes.
   * 
   * @param tag the git tag
   * @param name the release name
   * @param description the release's description
   * @param prerelease whether to mark the release as a 'prerelease' or not
   * @param repo the github repo
   * @since 0.2.0
   */
  @SuppressWarnings("checkstyle:ParameterNumber")
  Upload(String tag, String name, String description, boolean prerelease, Scalar<Repo> repo) {
    this(tag, name, description, null, prerelease, repo);
  }

  /**
   * For testing purposes.
   * 
   * @param tag the git tag
   * @param name the release name
   * @param description the release's description (has precedence over {@code descriptionFile}
   * @param descriptionFile the file from which to get the description
   * @param prerelease whether to mark the release as a 'prerelease' or not
   * @param repo the github repo
   * @since 0.3.0
   */
  @SuppressWarnings("checkstyle:ParameterNumber")
  Upload(
    String tag, String name, String description, File descriptionFile,
    boolean prerelease, Scalar<Repo> repo
  ) {
    this(tag, name, description, descriptionFile, prerelease, Collections.emptyList(), repo);
  }

  /**
   * For testing purposes.
   * 
   * @param tag the git tag
   * @param name the release name
   * @param description the release's description (has precedence over {@code descriptionFile}
   * @param descriptionFile the file from which to get the description
   * @param prerelease whether to mark the release as a 'prerelease' or not
   * @param assets the list of assets to attach to the release
   * @param repo the github repo
   * @since 0.4.0
   */
  @SuppressWarnings("checkstyle:ParameterNumber")
  Upload(
    String tag, String name, String description, File descriptionFile,
    boolean prerelease, List<Asset> assets, Scalar<Repo> repo
  ) {
    this.tag = tag;
    this.name = name;
    this.description = description;
    this.descriptionFromFile = descriptionFile;
    this.prerelease = prerelease;
    this.assets = assets;
    this.githubRepo = new IoCheckedScalar<>(repo);
  }

  @Override
  @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:NPathComplexity"})
  public void execute() throws MojoExecutionException, MojoFailureException {
    this.getLog().info(String.format(
      "Creating release with name %s and tag %s at %s/%s",
      this.name, this.tag, this.user, this.repo
    ));
    try {
      final Release.Smart release = new Release.Smart(
        this.githubRepo.value()
          .releases()
          .create(this.tag)
      );
      if (Objects.nonNull(this.name)) {
        release.name(this.name);
      }
      if (Objects.nonNull(this.description)) {
        release.body(this.description);
      } else if (Objects.nonNull(this.descriptionFromFile)) {
        release.body(
          new TextOf(this.descriptionFromFile).asString()
        );
      }
      release.prerelease(this.prerelease);
      for (Asset asset : Optional.ofNullable(this.assets).orElse(Collections.emptyList())) {
        release.assets().upload(
          new BytesOf(asset.getFile()).asBytes(),
          asset.getType(),
          asset.getName()
        );
      }
    } catch (IOException | IllegalArgumentException e) {
      throw new MojoFailureException("Error creating release", e);
    }
  }
}

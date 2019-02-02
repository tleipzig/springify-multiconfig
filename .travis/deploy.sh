#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
  echo "Decrypting deploy-key."
  openssl aes-256-cbc -K "${AES_PASSWORD}" -iv "${AES_IV}" -in ./.travis/deploy-key.gpg.enc -out ./.travis/deploy-key.gpg -d
  echo "Running uploadArchives."
  ./gradlew uploadArchives -PossrhUsername=${SONATYPE_USERNAME} -PossrhPassword=${SONATYPE_PASSWORD} -Psigning.keyId=${GPG_KEY_ID} -Psigning.password=${GPG_PASSPHRASE} -Psigning.secretKeyRingFile=.travis/deploy-key.gpg
fi

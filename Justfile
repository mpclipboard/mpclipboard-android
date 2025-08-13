clean:
    rm -rf build
    rm -rf clipboardlib/build

tag := "0.2.0"
push:
    git push --force
    git tag -d "{{tag}}" || true
    git push --delete origin "{{tag}}" || true
    git tag "{{tag}}"
    git push origin "{{tag}}"

rebuild:
    ./gradlew build --refresh-dependencies

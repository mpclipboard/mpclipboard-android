clean:
    rm -rf build
    rm -rf clipboardlib/build

release_url := "https://github.com/iliabylich/shared-clipboard-client-generic/releases/download/latest"
download-rustls-platform-verifier-android:
    rm -f rustls-platform-verifier-android.tar.gz
    wget -q {{release_url}}/rustls-platform-verifier-android.tar.gz -O rustls-platform-verifier-android.tar.gz

    rm -rf rustls-platform-verifier-android
    mkdir -p rustls-platform-verifier-android
    tar -xvzf rustls-platform-verifier-android.tar.gz -C rustls-platform-verifier-android
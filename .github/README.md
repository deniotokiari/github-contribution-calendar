# GitHub Actions CI Setup

This directory contains GitHub Actions workflows for continuous integration of the GitHub Contribution Calendar Android app.

## Workflows

### 1. `build.yml` - Build Android App
- **Triggers**: Push to main
- **Actions**: Builds the Android app
- **Notifications**: GitHub Actions UI notifications

### 2. `test.yml` - Run Unit Tests
- **Triggers**: Push to main
- **Actions**: Runs unit tests, generates test reports
- **Notifications**: GitHub Actions UI notifications

## No Configuration Required

These workflows run automatically without any additional setup. No GitHub secrets or email configuration is needed.

## Workflow Features

### ✅ What Each Workflow Does:

#### Build Workflow (`build.yml`)
- ✅ Checks out code
- ✅ Sets up JDK 17 and Android SDK
- ✅ Caches Gradle dependencies
- ✅ Builds debug APK
- ✅ Uploads build artifacts
- ✅ Provides GitHub Actions UI notifications

#### Test Workflow (`test.yml`)
- ✅ Checks out code
- ✅ Sets up JDK 17 and Android SDK
- ✅ Caches Gradle dependencies
- ✅ Runs unit tests (83 tests)
- ✅ Generates test reports
- ✅ Uploads test results
- ✅ Publishes test results
- ✅ Provides GitHub Actions UI notifications

### 📊 Test Coverage

The workflows will run **83 unit tests** covering:
- Core business logic (Result monad, extensions)
- Data models (value classes, serialization)
- Domain models (entities, error handling)
- Analytics (user interaction tracking)
- Use cases (business logic)

### 📧 GitHub Actions Notifications

You'll receive notifications in the GitHub Actions UI for:
- ✅ **Success**: Build/test completion with links to results
- ❌ **Failure**: Build/test failure with error details and logs
- 📎 **Artifacts**: Links to download build outputs and test reports

### 🚀 Performance

- **Caching**: Gradle dependencies are cached for faster builds
- **Parallel**: Tests and build run in the same job for efficiency
- **Artifacts**: Build outputs and test reports are preserved for 7 days

## Usage

### Automatic Triggers
- **Push to main**: All workflows run automatically
- **Pull requests**: All workflows run for validation

### Manual Triggers
You can also trigger workflows manually:
1. Go to **Actions** tab in your repository
2. Select the workflow you want to run
3. Click **Run workflow**

### Monitoring
- View workflow runs in the **Actions** tab
- Check GitHub Actions notifications for status updates
- Download artifacts from completed runs

## Troubleshooting

### Common Issues:

1. **Build fails**: Check Android SDK setup and dependencies
2. **Tests fail**: Review test logs in the Actions tab
3. **Gradle cache issues**: Clear cache by updating the cache key

### Getting Help:
- Check the Actions tab for detailed logs
- Review the test reports in artifacts
- Check the workflow files for configuration details

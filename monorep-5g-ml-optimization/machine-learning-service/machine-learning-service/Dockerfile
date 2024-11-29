# syntax=docker/dockerfile:1

ARG PYTHON_VERSION=3.12.7
FROM python:${PYTHON_VERSION}-slim as base

# Prevents Python from writing pyc files.
ENV PYTHONDONTWRITEBYTECODE=1

# Keeps Python from buffering stdout and stderr.
ENV PYTHONUNBUFFERED=1

WORKDIR /app


# Create a non-privileged user for the app.
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser

# Install dependencies with caching.
RUN --mount=type=cache,target=/root/.cache/pip \
    --mount=type=bind,source=requirements.txt,target=requirements.txt \
    python -m pip install -r requirements.txt

# Add curl installation to the Dockerfile
RUN apt update && apt install -y curl

# Switch to the non-privileged user.
USER appuser

# Copy the source code.
COPY . .

# Expose the port the application listens on.
EXPOSE 8000

# Run the application.
CMD waitress-serve --host=0.0.0.0 --port=8000 app:app

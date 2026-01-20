#!/usr/bin/env bash
set -euo pipefail

# Release/milestone version (edit as needed)
MILESTONE_VERSION="${MILESTONE_VERSION:-0.13.0}"

## PRE CONDITIONS

# This script requires the GitHub CLI (`gh`).
# On macOS and Linux, `gh` can be installed via Homebrew:
#   brew install gh
# On other *nix systems, see the official installation instructions at:
#   https://cli.github.com/
# Documentation for `gh` is available at:
#   https://cli.github.com/manual/
# After installation, authenticate once using:
#   gh auth login


# Repo to query (override via env if you want)
REPO="${REPO:-tdf/odftoolkit}"

# Emit markdown bullets for issues in the milestone, sorted by issue number.
# Notes:
# - "merged" == issue was closed via a PR (closedByPullRequestsReferences non-empty)
# - "closed" == issue was closed without a PR
# - Date includes year
gh issue list \
  --repo "$REPO" \
  --state closed \
  --search "milestone:${MILESTONE_VERSION}" \
  --limit 300 \
  --json number,title,author,closedAt,closedByPullRequestsReferences,url \
  --template '{{range .}}{{ $prs := .closedByPullRequestsReferences }}#{{.number}}|* [#{{.number}}]({{.url}}) {{.title}} - by {{.author.login}}{{if gt (len $prs) 0}} was merged on {{timefmt "Jan 2, 2006" .closedAt}}{{else}} was closed on {{timefmt "Jan 2, 2006" .closedAt}}{{end}}
{{end}}' \
| sort -n -t'#' -k2 \
| cut -d'|' -f2-

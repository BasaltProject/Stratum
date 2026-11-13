# Security policy for Basalt Stratum

## Report vulnerabilities privately

**Do not disclose suspected vulnerabilities through public issues, pull requests,
logs or discussions.** Use [GitHub's private vulnerability reporting form](https://github.com/BasaltProject/Stratum/security/advisories/new).
It sends the report privately to the repository's security maintainers.

If you cannot use that form, email
[hello@basaltmc.org](mailto:hello@basaltmc.org) with **Basalt Stratum security report** in the subject.
Avoid sending live secrets and ask how to share sensitive evidence if necessary.

Stratum is built into Basalt Server and Basalt Proxy. For issues in those products, see their
security policies ([Server](https://github.com/BasaltProject/Server/blob/main/SECURITY.md),
[Proxy](https://github.com/BasaltProject/Proxy/blob/main/SECURITY.md)). If several components are
involved, identify all of them; you do not need a separate public report in each repository.

## Scope

Security investigation focuses on the latest published stable release and the current `main`
and nightly builds. Backports and long-term support are not guaranteed.

Compatibility with a Java plugin API is not a security sandbox for arbitrary plugin code. Report the
component and platform actually used (Bukkit, BungeeCord or Velocity) and the plugins involved.

## What to include

- A concise description, affected component, impact and required attacker access.
- Exact Basalt version or commit, OS, CPU architecture and relevant plugin versions.
- Minimal steps or a small proof of concept tested only in an environment you control.
- Redacted logs or crash traces.

Do not include live credentials, tokens, private keys or personal data, and do not attack other
people's servers to demonstrate a finding. This policy does not promise a response deadline, a fix
schedule or a bounty.

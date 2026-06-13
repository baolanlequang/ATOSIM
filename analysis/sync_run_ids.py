#!/usr/bin/env python3
"""Sync result_run_*.json filenames and their "runId" field to their "config_id".

Example: result_run_1.json with config_id "10" becomes result_run_10.json,
and its "runId" field is updated from 1 to 10.
"""
import argparse
import json
import sys
from pathlib import Path


def sync_run_ids(folder: Path, pattern: str, recursive: bool) -> None:
    files = sorted((folder.rglob if recursive else folder.glob)(pattern))
    if not files:
        print(f"No files matching '{pattern}' found in {folder}")
        return

    renames = []  # (old_path, new_path)
    for path in files:
        with path.open("r", encoding="utf-8") as f:
            data = json.load(f)

        config_id = int(data["config_id"])
        data["runId"] = config_id

        with path.open("w", encoding="utf-8") as f:
            json.dump(data, f, indent=2)
            f.write("\n")

        new_path = path.with_name(f"result_run_{config_id}.json")
        if new_path != path:
            renames.append((path, new_path))

    if not renames:
        print(f"Updated runId in {len(files)} file(s); no renames needed.")
        return

    # Two-phase rename so swaps (e.g. 1<->10) don't clobber each other.
    staged = []
    for old_path, new_path in renames:
        tmp_path = old_path.with_name(old_path.name + ".tmp")
        old_path.rename(tmp_path)
        staged.append((tmp_path, new_path))

    for tmp_path, new_path in staged:
        if new_path.exists():
            raise FileExistsError(
                f"Cannot rename {tmp_path} -> {new_path}: target already exists "
                "(duplicate config_id?)"
            )
        tmp_path.rename(new_path)
        print(f"{tmp_path.name[:-4]} -> {new_path.name}")

    print(f"Updated runId in {len(files)} file(s); renamed {len(renames)} file(s).")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("folder", type=Path, help="Folder containing result_run_*.json files")
    parser.add_argument(
        "--pattern", default="result_run_*.json", help="Glob pattern to match (default: %(default)s)"
    )
    parser.add_argument(
        "--recursive", action="store_true", help="Search the folder recursively"
    )
    args = parser.parse_args()

    if not args.folder.is_dir():
        sys.exit(f"Not a directory: {args.folder}")

    sync_run_ids(args.folder, args.pattern, args.recursive)


if __name__ == "__main__":
    main()

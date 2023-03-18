# kindle-to-notion

Export highlights made on a Kindle device to a custom Notion page. This is achieved by parsing the data stored on the `My Clippings.txt` file on the Kindle, and uploading it via the Notion API.

## Bugs

### Overlapping clippings

- The `My Clippings.txt` file on Kindle devices does not delete data from the file when a highlight is removed from text.
- When a new annotation is added, the device appends it to the existing file without deleting any previous annotations.
- This can result in multiple clippings with overlapping text. For example, we may extract the following highlights for a book:

```clojure
({:title "How to Win Friends and Influence People",
  :author "Dale Carnegie",
  :start-loc 88,
  :end-loc 91,
  :added-at "Thursday, 23 February 2023 21:52:00",
  :text "By criticizing, we do not make lasting changes and often incur resentment. Hans"}
 {:title "How to Win Friends and Influence People",
  :author "Dale Carnegie",
  :start-loc 88,
  :end-loc 90,
  :added-at "Thursday, 23 February 2023 21:52:14",
  :text "By criticizing, we do not make lasting changes and often incur resentment."})
```

- In the example above, I included "Hans" at the end of the first highlight by mistake, and later edited it to remove that part.
- Despite the edit, both the original and edited highlights are present in the `My Clippings.txt` file.
- To avoid this, it would be useful to identify overlapping highlights and keep only the most recent one based on the `:added-at` value.

## License

Copyright © 2023 Vimanyu Taneja

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

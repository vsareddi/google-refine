/*

Copyright 2010, Google Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
    * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.google.refine.exporters;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import com.google.refine.ProjectManager;
import com.google.refine.browsing.Engine;
import com.google.refine.browsing.FilteredRows;
import com.google.refine.browsing.RowVisitor;
import com.google.refine.model.Cell;
import com.google.refine.model.Column;
import com.google.refine.model.Project;
import com.google.refine.model.Row;

public class HtmlTableExporter implements WriterExporter {

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public void export(Project project, Properties options, Engine engine, Writer writer) throws IOException {
        writer.write("<html>\n");
        writer.write("<head><title>"); 
        writer.write(ProjectManager.singleton.getProjectMetadata(project.id).getName());
        writer.write("</title></head>\n");

        writer.write("<body>\n");
        writer.write("<table>\n");
        
        writer.write("<tr>");
        {
            for (Column column : project.columnModel.columns) {
                writer.write("<th>");
                writer.write(column.getName());
                writer.write("</th>");
            }
        }
        writer.write("</tr>\n");
        
        {
            RowVisitor visitor = new RowVisitor() {
                Writer writer;
                
                public RowVisitor init(Writer writer) {
                    this.writer = writer;
                    return this;
                }
                
                @Override
                public void start(Project project) {
                    // nothing to do
                }
                
                @Override
                public void end(Project project) {
                    // nothing to do
                }
                
                @Override
                public boolean visit(Project project, int rowIndex, Row row) {
                    try {
                        writer.write("<tr>");
                        
                        for (Column column : project.columnModel.columns) {
                            writer.write("<td>");
                            
                            int cellIndex = column.getCellIndex();
                            if (cellIndex < row.cells.size()) {
                                Cell cell = row.cells.get(cellIndex);
                                if (cell != null && cell.value != null) {
                                    Object v = cell.value;
                                    writer.write(v instanceof String ? ((String) v) : v.toString());
                                }
                            }
                            
                            writer.write("</td>");
                        }
                        
                        writer.write("</tr>\n");
                    } catch (IOException e) {
                        // ignore
                    }
                    return false;
                }
            }.init(writer);
            
            FilteredRows filteredRows = engine.getAllFilteredRows();
            filteredRows.accept(project, visitor);
        }
        
        writer.write("</table>\n");
        writer.write("</body>\n");
        writer.write("</html>\n");
    }

}

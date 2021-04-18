/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.models.core;

/**
 * Any requests from the UI that need authentication will be accompanied by a corresponding Firebase authToken: this
 * will be checked before any transactions are committed.
 * Source and target refer to graph entities that are the subject of the request.
 * Task ID refers to a "fos_task".
 */
public class RequestWithAuth {

    String taskId;
    String source;
    String target;

    public String getSource() {
        return source;
    }

    public RequestWithAuth setSource(String source) {
        this.source = source;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public RequestWithAuth setTarget(String target) {
        this.target = target;
        return this;
    }

    public String getTaskId() {
        return taskId;
    }

    public RequestWithAuth setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }
}

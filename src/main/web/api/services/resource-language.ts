/**
 * ResearchSpace
 * Copyright (C) 2020, © Trustees of the British Museum
 * Copyright (C) 2015-2019, metaphacts GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import { Rdf } from 'platform/api/rdf';
import { QueryContext } from 'platform/api/sparql';

import { BaseResourceService } from './BaseResourceService';

const LANGUAGES_SERVICE_URL = '/rest/data/rdf/utils/getLanguages';
const service = new (class extends BaseResourceService {
  constructor() {
    super(LANGUAGES_SERVICE_URL);
  }

  createRequest(resources: string[], repository: string) {
    const request = super.createRequest(resources, repository);
    return request;
  }
})();

export function getLanguage(iri: Rdf.Iri, options?: { context?: QueryContext }) {
  return service.getResource(iri, options ? options.context : undefined);
}

export function getLanguages(iris: ReadonlyArray<Rdf.Iri>, options?: { context?: QueryContext }) {
  return service.getResources(iris, options ? options.context : undefined);
}

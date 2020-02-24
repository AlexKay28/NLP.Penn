package relationsPhase2;

/* Copyright (c) 2010 the authors listed at the following URL, and/or
the authors of referenced articles or incorporated external code:
http://en.literateprograms.org/Dijkstra's_algorithm_(Java)?action=history&offset=20081113161332

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Retrieved from: http://en.literateprograms.org/Dijkstra's_algorithm_(Java)?oldid=15444
 */

import java.util.PriorityQueue;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;







public class Dijkstra
{
	public void computePaths(Vertex source)
	{
		source.minDistance = 0.;
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(source);

		while (!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();

			// Visit each edge exiting u
			for (Edge e : u.adjacencies)
			{
				Vertex v = e.target;
				double weight = e.weight;
				double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance) {
					vertexQueue.remove(v);

					v.minDistance = distanceThroughU ;
					v.previous = u;
					vertexQueue.add(v);

				}

			}
		}
	}


	public List<Vertex> getShortestPathTo(Vertex target)
	{
		List<Vertex> path = new ArrayList<Vertex>();
		for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
			path.add(vertex);

		Collections.reverse(path);
		return path;
	}

	public static void main(String[] args)
	{
		/*Vertex v0 = new Vertex("Harrisburg");
		Vertex v1 = new Vertex("Baltimore");
		Vertex v2 = new Vertex("Washington");
		Vertex v3 = new Vertex("Philadelphia");
		Vertex v4 = new Vertex("Binghamton");
		Vertex v5 = new Vertex("Allentown");
		Vertex v6 = new Vertex("New York");
		v0.adjacencies = new Edge[]{ new Edge(v1,  79.83),
				new Edge(v5,  81.15) };
		v1.adjacencies = new Edge[]{ new Edge(v0,  79.75),
				new Edge(v2,  39.42),
				new Edge(v3, 103.00) };
		v2.adjacencies = new Edge[]{ new Edge(v1,  38.65) };
		v3.adjacencies = new Edge[]{ new Edge(v1, 102.53),
				new Edge(v5,  61.44),
				new Edge(v6,  96.79) };
		v4.adjacencies = new Edge[]{ new Edge(v5, 133.04) };
		v5.adjacencies = new Edge[]{ new Edge(v0,  81.77),
				new Edge(v3,  62.05),
				new Edge(v4, 134.47),
				new Edge(v6,  91.63) };
		v6.adjacencies = new Edge[]{ new Edge(v3,  97.24),
				new Edge(v5,  87.94) };
		Vertex[] vertices = { v0, v1, v2, v3, v4, v5, v6 };*/

		Vertex v0 = new Vertex("0");
		Vertex v1 = new Vertex("1");
		Vertex v2 = new Vertex("2");
		Vertex v3 = new Vertex("3");
		Vertex v4 = new Vertex("4");
		Vertex v5 = new Vertex("5");
		Vertex v6 = new Vertex("6");
		Vertex v7 = new Vertex("7");
		Vertex v8 = new Vertex("8");
		
		v0.adjacencies = new Edge[]{new Edge(v2,1) };
		v1.adjacencies = new Edge[]{new Edge(v2,1) };
		v2.adjacencies = new Edge[]{new Edge(v0,1), new Edge(v1,1), new Edge(v5,1), new Edge(v7,1) };
		v3.adjacencies = new Edge[]{};
		v4.adjacencies = new Edge[]{new Edge(v5,1) };
		v5.adjacencies = new Edge[]{new Edge(v2,1), new Edge(v4,1), new Edge(v7,1) };
		v6.adjacencies = new Edge[]{ };
		v7.adjacencies = new Edge[]{new Edge(v2,1), new Edge(v5,1)  };
		v8.adjacencies = new Edge[]{};
		Vertex[] vertices = { v0, v1, v2, v3, v4, v5, v6,v7,v8 };
		
		Dijkstra d = new Dijkstra();

		d.computePaths(v2);
		for (Vertex v : vertices)
		{
			System.out.println("Distance to " + v + ": " + v.minDistance);
			List<Vertex> path = d.getShortestPathTo(v);
			System.out.println("Path: " + path);
		}

	}
}


# RecorderX
RecorderX 🎤🔴 - very simple Sound Recorder App with MVVM Architecture ,Hilt for the Dependency Injection 💉 and Exo-Player ▶️

### Tech stack and 3Pr libs :-
- The entire app writen with [Kotlin](https://kotlinlang.org/).
- [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) for asynchronous database tasks.
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for dependency injection.
- [Exo-Player](https://github.com/google/ExoPlayer) to playing the records.
- [Timber](https://github.com/JakeWharton/timber) for debugging.
- JetPack
  - LiveData.
  - Lifecycle.
  - ViewModel.
  - Room Persistence.
- Architecture
  - MVVM Architecture 
  - "One Repository" pattern. "App have fewe requirements"
  
 ### Coming changes :-
 - Optimize The UI with the Material Component.
 - deal with [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) to make FileObserver run in the background to
    handle the case when the user delete record/'s to delete it / them from the database.
 
 ## License

	Copyright (C) 2020 nedaluOf
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may not
	use this file except in compliance with the License. You may obtain a copy of
	the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	License for the specific language governing permissions and limitations under
	the License.Pr

function(doc){
  if(doc.title && doc.tags){
      emit(doc.title, true);
  } else {
	  emit(doc.title, false);
  }
}